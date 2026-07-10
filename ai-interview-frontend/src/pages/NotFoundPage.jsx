import { Link } from "react-router";

function NotFoundPage() {
  return (
    <main className="page-container center-content">
      <h1>404</h1>
      <p>The page you requested does not exist.</p>

      <Link to="/" className="button button-primary">
        Return Home
      </Link>
    </main>
  );
}

export default NotFoundPage;