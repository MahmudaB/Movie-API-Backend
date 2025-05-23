﻿# Movie-API-Backend

#Authentication APIs (/api/v1/auth/)
POST /register – Register a new user.

POST /login – Login with credentials and receive an access + refresh token.

POST /refresh – Refresh expired access tokens using a valid refresh token.

#File Upload APIs (/file/)
POST /upload – Uploads an image file (e.g., movie poster) to the server.

GET /{fileName} – Serves a file by its name as a PNG image.

#Movie APIs (/api/v1/movie)
POST /add-movie – [ADMIN only] Adds a new movie with metadata and poster.

GET /{movieId} – Fetch movie details by ID.

GET / – Get a list of all movies.

PUT /{movieId} – Update movie details and optionally update the poster.

DELETE /{movieId} – Delete a movie by ID.

GET /allMoviesPage – Paginated movie list.

GET /allMoviesPageSort – Paginated + sorted movie list by fields (e.g., title, release date).
