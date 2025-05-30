use std::fs;
use std::io;
use std::path::Path;

fn main() -> io::Result<()> {
    clean();
    fs::create_dir_all("docs/posts").expect("should be able to create docs directory");
    let base = fs::read_to_string("public/base.html")?;
    build_pages(&base)?;
    build_posts(&base)?;
    copy_assets()?;
    println!("Done");
    Ok(())
}

fn clean() {
    if let Err(e) = fs::remove_dir_all("docs") {
        if e.kind() != io::ErrorKind::NotFound {
            eprintln!("Error removing directory: {}", e);
        }
    }
}

fn build_pages(base: &str) -> io::Result<()> {
    println!("Building pages...");
    for entry in fs::read_dir("public/pages")? {
        let entry = entry?;
        let path = entry.path();
        let content = fs::read_to_string(&path)?;
        let new_content = base.replace("{{content}}", &content);
        let new_path = path.strip_prefix("public/pages").expect("should be able to remove 'public/pages'");
        let destination = Path::new("docs").join(new_path);
        fs::write(destination, new_content)?;
    }
    Ok(())
}

fn build_posts(base: &str) -> io::Result<()> {
    println!("Building posts...");
    for entry in fs::read_dir("public/posts")? {
        let entry = entry?;
        let path = entry.path();
        let content = fs::read_to_string(&path)?;
        let new_content = base.replace(r#"href=""#, r#"href="../"#).replace("{{content}}", &content);
        let new_path = path.strip_prefix("public").expect("should be able to remove 'public'");
        let destination = Path::new("docs").join(new_path);
        fs::write(destination, new_content)?;
    }
    Ok(())
}

fn copy_assets() -> io::Result<()> {
    println!("Copying assets...");
    for entry in fs::read_dir("public/assets")? {
        let entry = entry?;
        let path = entry.path();
        let new_path = path.strip_prefix("public/assets").expect("should be able to remove 'public'");
        let destination = Path::new("docs").join(new_path);
        fs::copy(path, destination)?;
    }
    Ok(())
}
