package hu.tvarga.popularmovies.dataaccess;

import java.io.Serializable;

import hu.tvarga.popularmovies.utility.GsonHelper;

abstract class DAO implements Serializable {

	private static final long serialVersionUID = -7045461130437698110L;

	@Override
	public String toString() {
		return GsonHelper.getGson().toJson(this);
	}
}


