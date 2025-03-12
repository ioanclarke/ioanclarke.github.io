alias d := deploy

build:
    cargo build --release

create:
    ./target/release/my-ssg-rust

deploy:
    just build && ./target/release/my-ssg-rust && git add . && git commit && git push