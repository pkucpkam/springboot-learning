import Head from "next/head";

function Head1() {
  return (
    <Head>
      <title>My Blog - Học Next.js</title>
      <meta
        name="description"
        content="Khóa học Next.js từ cơ bản đến nâng cao."
      />
      <meta property="og:title" content="My Blog" />
      <meta property="og:image" content="/cover.png" />
    </Head>
  );
}

export default Head1;
