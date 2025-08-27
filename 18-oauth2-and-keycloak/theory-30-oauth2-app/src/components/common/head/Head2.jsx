import Head from "next/head";

function Head1() {
  return (
    <Head>
      <title>{post.title}</title>
      <meta name="description" content={post.excerpt} />
    </Head>
  );
}

export default Head1;
