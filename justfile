alias d := deploy

build:
    cargo build --release

deploy:
    just build && ./target/release/my-ssg-rust && git add . && git commit -m "Commit message - $(date '+%Y-%m-%d %H:%M:%S')" && git push