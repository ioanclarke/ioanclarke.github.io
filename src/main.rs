use std::fs;
use std::io;
use std::path::Path;

const CONTENT_INSERTION_POINT: &'static str = "{{content}}";
const INPUT_DIRECTORY: &'static str = "public";

fn main() -> io::Result<()> {
    clean()?;
    create_dirs()?;
    let base = fs::read_to_string(Path::new(INPUT_DIRECTORY).join("base.html"))?;
    build_pages(&base)?;
    build_posts(&base)?;
    copy_assets()?;
    println!("Done");
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
            replacer: |base, content| {
                base .replace(CONTENT_INSERTION_POINT, &content)
            },
        }
    )
}

fn build_posts(base: &str) -> io::Result<()> {
    generate(
        "posts",
        "posts",
        FsAction::WriteContent {
            base: String::from(base),
            replacer: |base, content| {
                base.replace(r#"href=""#, r#"href="../"#)
                    .replace("{{content}}", content)
            },
        },
    )
}

fn copy_assets() -> io::Result<()> {
    generate("assets", "", FsAction::CopyFile)
}

fn generate(src_dir: &str, dest_dir: &str, fs_action: FsAction) -> io::Result<()> {
    println!("Reading {}", src_dir);
    let src_path = Path::new(INPUT_DIRECTORY).join(src_dir);
    for entry in fs::read_dir(src_path)? {
        let entry = entry?;
        let path = entry.path();
        let file_name = path
            .file_name()
            .expect(format!("should be to get file name from '{}", path.to_str().unwrap()).as_str());
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
