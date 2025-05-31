alias d := deploy

launch:
    @open ./docs/index.html
    @just watch

format:
    cargo fmt
    cargo clippy --fix --allow-dirty --allow-staged

build:
    cargo build --release

create:
    @echo 'Building site...'
    @./target/release/my-ssg-rust

watch:
    @echo 'Watching for changes in ./public'
    @find public -type f | entr -s 'just create'

deploy: format build create
    git add .
    git commit
    git push
