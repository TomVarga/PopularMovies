package hu.tvarga.popularmovies;

class LoaderNotImplementedExceptions extends RuntimeException {

	private static final long serialVersionUID = 4206882973933069026L;

	LoaderNotImplementedExceptions(int loaderId) {
		super("Loader Not Implemented: " + loaderId);
	}
}
