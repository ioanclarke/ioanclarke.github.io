// highlight.mjs
import { codeToHtml } from "shiki";

const code = await readStdin();
const html = await codeToHtml(code, {
  lang: "java",
  theme: "github-light",
  transformers: [
    {
      pre(node) {
        delete node.properties.style;
      },
    },
  ],
});

process.stdout.write(html);

function readStdin() {
  return new Promise((resolve) => {
    let data = "";
    process.stdin.on("data", (c) => (data += c));
    process.stdin.on("end", () => resolve(data));
  });
}
