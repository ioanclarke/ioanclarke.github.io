alias d := deploy

dev:
    @just build
    @open ./docs/index.html
    @echo 'Watching for changes in ./public'
    @find public -type f | entr -s 'just create'

format:
    cargo fmt
    cargo clippy --fix --allow-dirty --allow-staged

build:
    cargo build --release

create:
    @./target/release/my-ssg-rust

deploy: format build create
    git add .
    git commit
    git push
