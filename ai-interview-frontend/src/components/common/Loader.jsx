function Loader({ message = "Loading..." }) {
  return (
    <div className="loader-container">
      <div className="spinner" />
      <p>{message}</p>
    </div>
  );
}

export default Loader;