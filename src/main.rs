use kuchiki::traits::TendrilSink;
use kuchiki::NodeRef;
use std::fs;
use std::io;
use std::io::Write;
use std::path::Path;
use std::process::{Command, Stdio};

const CONTENT_INSERTION_POINT: &str = "{{content}}";
const INPUT_DIRECTORY: &str = "public";

fn main() -> io::Result<()> {
    clean()?;
    create_dirs()?;
    let base = fs::read_to_string(Path::new(INPUT_DIRECTORY).join("base.html"))?;
    build_pages(&base)?;
    build_posts(&base)?;
    copy_assets()?;
    println!("Done!");
    Ok(())
}

fn clean() -> io::Result<()> {
    if let Err(e) = fs::remove_dir_all("docs") {
        if e.kind() != io::ErrorKind::NotFound {
            return Err(e);
        }
    }
    Ok(())
}

fn create_dirs() -> io::Result<()> {
    fs::create_dir_all("docs/posts")
}

fn build_pages(base: &str) -> io::Result<()> {
    generate(
        "pages",
        "",
        FsAction::WriteContent {
            base: String::from(base),
            replacer: |base, content| base.replace(CONTENT_INSERTION_POINT, content),
        },
    )
}

fn build_posts(base: &str) -> io::Result<()> {
    generate(
        "posts",
        "posts",
        FsAction::WriteContent {
            base: String::from(base),
            replacer: |base, content| {
                let html_input = base.replace(CONTENT_INSERTION_POINT, content);
                let document = kuchiki::parse_html().one(html_input);
                let document = highlight_code(document);
                let document = fix_relative_links(document);
                let document = make_external_links_safe(document);
                let mut output = Vec::new();
                document.serialize(&mut output).unwrap();
                String::from_utf8(output).unwrap()
            },
        },
    )
}

fn copy_assets() -> io::Result<()> {
    generate("assets", "", FsAction::CopyFile)
}

fn generate(src_dir: &str, dest_dir: &str, fs_action: FsAction) -> io::Result<()> {
    let src_path = Path::new(INPUT_DIRECTORY).join(src_dir);
    println!("Reading {}...", src_path.to_str().unwrap());
    for entry in fs::read_dir(src_path)? {
        let entry = entry?;
        let path = entry.path();
        println!("Reading {}", path.to_str().unwrap());
        let file_name = path.file_name().unwrap_or_else(|| {
            panic!(
                "should be to get file name from '{}",
                path.to_str().unwrap()
            )
        });
        let destination = Path::new("docs").join(dest_dir).join(file_name);

        match fs_action {
            FsAction::WriteContent { ref base, replacer } => {
                let content = fs::read_to_string(path)?;
                let new_content = replacer(base.as_str(), content.as_str());
                fs::write(destination, new_content)?;
            }
            FsAction::CopyFile => {
                fs::copy(path, destination)?;
            }
        }
    }
    Ok(())
}

enum FsAction {
    WriteContent {
        base: String,
        replacer: fn(&str, &str) -> String,
    },
    CopyFile,
}

fn highlight_code(document: NodeRef) -> NodeRef {
    let code_node_refs: Vec<_> = document.select("pre > code").unwrap().collect();

    for code_node_ref in code_node_refs {
        let code_node = code_node_ref.as_node();
        let pre_node = code_node.parent().unwrap();

        let raw_code = code_node.text_contents();

        let mut highlight_task = Command::new("node")
            .arg("src/highlight.mjs")
            .stdin(Stdio::piped())
            .stdout(Stdio::piped())
            .spawn()
            .unwrap();

        let mut stdin = highlight_task
            .stdin
            .take()
            .expect("Should be able to open stdin");
        std::thread::spawn(move || {
            stdin
                .write_all(raw_code.as_bytes())
                .expect("Code should be able to be written to standard");
        });

        let highlighted_html =
            String::from_utf8(highlight_task.wait_with_output().unwrap().stdout).unwrap();

        let new_fragment = kuchiki::parse_html().one(highlighted_html);

        let new_pre = new_fragment.select_first("pre").unwrap().as_node().clone();

        pre_node.insert_after(new_pre);
        pre_node.detach();
    }
    document
}

fn fix_relative_links(document: NodeRef) -> NodeRef {
    for link in document.select(r#"[href]"#).unwrap() {
        let mut attrs = link.attributes.borrow_mut();
        let href = match attrs.get("href") {
            Some(h) => h.to_owned(),
            None => break,
        };

        if !href.starts_with("https://") {
            attrs.insert("href", format!("../{}", href));
        }
    }
    document
}

fn make_external_links_safe(document: NodeRef) -> NodeRef {
    for link in document.select("a[href]").unwrap() {
        let mut attrs = link.attributes.borrow_mut();

        let href = match attrs.get("href") {
            Some(h) => h,
            None => continue,
        };

        let is_external = href.starts_with("https://") || href.starts_with("http://");

        if !is_external {
            continue;
        }

        attrs.insert("target", String::from("_blank"));
        attrs.insert("rel", String::from("noopener noreferrer"));
    }
    document
}
