This is a basic static site generator that I use to generate my personal website.

Instructions:

1. Put common layout in `public/base.html` - the string `{{content}}` will be replaced by the content of the individual pages
2. Put top-level pages you want (`index.html`, `projects.html`, `posts.html` etc.) in `public/pages` directory
3. Put assets (favicon, CSS etc.) in `public/assets` directory
4. Put posts in `public/posts` directory
5. Run `just deploy` - static site files will be outputted to `docs` directory (install just using `brew install just` if needed)

Code blocks, marked by `<pre><code>...</code></pre>`, will be assumed to be Java code and will have syntax highlighing added (a Node script
is used to do this since `shiki` is not available in Rust)

TODO:
- Look into using Rust library for syntax highlighting to remove Node dependency
