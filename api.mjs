export const search = async (query) => {
  const searchUrl = "https://api.tvmaze.com/search/shows?q=";
  const response = await fetch(`${searchUrl}${encodeURIComponent(query)}`);
  const data = await response.json();
  console.log("data is", data);
  return data.map(({ show }) => show);
};

export const getSeasons = async (showId) => {
  console.log("getting seasons for show", showId);
};

export const addShow = async (showId, seasonId) => {};

export const startWatching = async (showId, seasonId, startDate) => {};

export const endWatching = async (showId, seasonId, endDate) => {};
