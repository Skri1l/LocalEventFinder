# API 

## Response Format

There are only two types of responses to requests:
- Data Responce
- Error Responce

### Data Responce

For one entity:
```json
{
  "data": {
    /* data responce body */
  }
}
```

For list of entities:
```json
{
  "data": [
    {
      /* data responce body */
    },
    {
      /* data responce body */
    }
  ]
}
```

### Error Responce

```json
{
  "error": {
    "code": 404,
    "message": "User not found",
    "details": "User ID 123 not exists in data base"
  }
}
```

## Health

**Get** `/health`

**Requires authorization:** No

If everything OK **Response:**

```json
{
  "data": {
    "status": "OK"
  }
}
```

## Authentication

### Auth Header

```
Authorization: Bearer <access_token>
```

### Login

**POST** `/auth/login`

**Requires authorization:** No

**Request:**

```json
{
  "email": "john@example.com",
  "password": "password123"
}
```

**Response:**

```json
{
  "data": {
    "access_token": "jwt",
    "refresh_token": "jwt",
    "expires_in": 3600
  }
}
```

### Logout

**DELETE** `/auth/logout`

**Requires authorization:** Yes

**Responce:**
```json
{
  "data": {
    "status": "OK"
  }
}
```

### Register User

**POST** `/auth/register`

**Requires authorization:** No

**Request:**
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "StrongPassword123!",
  "avatar_url": "https://example.com/avatar.png",
  "age": 25
}
```

**Response:**

```json
{
  "data": {
    "access_token": "jwt",
    "refresh_token": "jwt",
    "expires_in": 3600
  }
}
```

### Refresh token

**POST** `/auth/refresh`

**Requires authorization:** No

**Request:**
```json
{
  "refreshToken": "jwt"
}
```

**Response:**

```json
{
  "data": {
    "access_token": "jwt",
    "refresh_token": "jwt",
    "expires_in": 3600
  }
}
```

## Users

### Get User

**GET** `/users/{id}`

**Requires authorization:** Yes

**Response:**

```json
{
  "data": [
    {
      "username": "john_doe",
      "email": "john@example.com",
      "avatar_url": "https://example.com/avatar.png",
      "age": 25
    },
    /* etc. */
  ]
}
```


### Update User (Partial)

**PATCH** `/users/me`

**Requires authorization:** Yes - A user can only edit their own profile.

**Request Example:**

```json
{
  "username": "new_name"
}
```

**Response:**

```json
{
  "data": 
  {
    "username": "john_doe",
    "email": "john@example.com",
    "password": "StrongPassword123!",
    "avatar_url": "https://example.com/avatar.png",
    "age": 25
  }
}
```

### Delete User

**DELETE** `/users/me`

**Requires authorization:** Yes - A user can only delete their own profile.

If everything OK **Response:**

```json
{
  "data": {
    "status": "OK"
  }
}
```

## Users Admin Control

### Block User

**PATCH** `/users/{id}/block`

**Requires authorization:** Yes - Only admin can block users.

If everything OK **Response:**

```json
{
  "data": {
    "status": "OK"
  }
}
```

### UnBlock User

**DELETE** `/users/{id}/block`

**Requires authorization:** Yes - Only admin can block users.

If everything OK **Response:**

```json
{
  "data": {
    "status": "OK"
  }
}
```

### Enhance the role of User

**PATCH** `/users/{id}/op`

**Requires authorization:** Yes - Only admin can Enhance role users.

If everything OK **Response:**

```json
{
  "data": {
    "status": "OK"
  }
}
```

### Downgrade the role of User

**DELETE** `/users/{id}/op`

**Requires authorization:** Yes - Only admin can Enhance role users.

If everything OK **Response:**

```json
{
  "data": {
    "status": "OK"
  }
}
```

## Events

### Create Event

**POST** `/events`

**Requires authorization:** Yes

**Request:**

```json
{
  "title": "Music Festival",
  "description": "Outdoor event",
  "latitude": 54.6872,
  "longitude": 25.2797,
  "start_time": "2026-06-01T10:00:00Z",
  "end_time": "2026-06-01T18:00:00Z",
  "max_participants": 100,
  "age_restriction": 18,
  "image_url": "...",
  "status": "upcoming",
  "user_id": 27
}
```

> for first versions uses user_id for created_by -> in future removes from api   
> `created_by` tooks from tocken

**Responce:**
```json
{
  "data": {
    "status": "OK"
  }
}
```

### Get Event

**GET** `/events/{id}`

**Requires authorization:** Yes

**Responce:**

```json
{
  "data":
  {
    "id": 12,
    "title": "Music Festival",
    "description": "Outdoor event",
    "latitude": 54.6872,
    "longitude": 25.2797,
    "start_time": "2026-06-01T10:00:00Z",
    "end_time": "2026-06-01T18:00:00Z",
    "max_participants": 100,
    "age_restriction": 18,
    "image_url": "...",
    "status": "upcoming",
    "created_by": 27,
    "created_at": "2026-06-01T10:00:00Z"
  }
}
```

### List Events

**GET** `/events`

**Requires authorization:** Yes

**Query parameters:**

```
?city=Vilnius
&country=Lithuania
&category_id=uuid
&tag_id=uuid
&date_from=2026-01-01
&date_to=2026-12-31
&limit=20
&offset=0
```

**Responce:**

```json
{
  "data": [
    {
      "id": 12,
      "title": "Music Festival",
      "description": "Outdoor event",
      "latitude": 54.6872,
      "longitude": 25.2797,
      "start_time": "2026-06-01T10:00:00Z",
      "end_time": "2026-06-01T18:00:00Z",
      "max_participants": 100,
      "age_restriction": 18,
      "image_url": "...",
      "status": "upcoming",
      "created_by": 27,
      "created_at": "2026-06-01T10:00:00Z"
    },
    /* etc. */
  ]
}
```

### Update Event

**PATCH** `/events/{id}`

**Requires authorization:** Yes - only user that created by could edit this event.

```json
{
  "data":
  {
    "id": 12,
    "title": "Music Festival",
    "description": "Outdoor event",
    "latitude": 54.6872,
    "longitude": 25.2797,
    "start_time": "2026-06-01T10:00:00Z",
    "end_time": "2026-06-01T18:00:00Z",
    "max_participants": 100,
    "age_restriction": 18,
    "image_url": "...",
    "status": "upcoming",
    "created_by": 27,
    "created_at": "2026-06-01T10:00:00Z"
  }
}
```

### Delete Event

**DELETE** `/events/{id}`

**Requires authorization:** Yes - only user that created by could delete this event.

If everything OK **Response:**

```json
{
  "data": {
    "status": "OK"
  }
}
```

## Event Participants

### Join Event

**POST** `/events/{id}/participants`

**Requires authorization:** Yes

> The user is identified by the token (from header), `user_id` is not transmitted.

If everything OK **Response:**

```json
{
  "data": {
    "status": "OK"
  }
}
```

### Leave Event

**DELETE** `/events/{id}/participants/me`

**Requires authorization:** Yes

If everything OK **Response:**

```json
{
  "data": {
    "status": "OK"
  }
}
```

### Get Participants

**GET** `/events/{id}/participants`

**Requires authorization:** Yes

**Response:**

```json
{
  "data": [
    {
      "username": "john_doe",
      "email": "john@example.com",
      "avatar_url": "https://example.com/avatar.png",
      "age": 25
    },
    /* etc. */
  ]
}
```

## Categories

### Create Category

**POST** `/categories`

**Request:**

```json
{
  "name": "Sports"
}
```

**Requires authorization:** Yes

If everything OK **Response:**

```json
{
  "data": {
    "status": "OK"
  }
}
```

### List Categories

**GET** `/categories`

**Requires authorization:** No

**Response:**

```json
{
  "data": [
    {
      "name": "Sports"
    },
    /* etc. */
  ]
}
```

### Assign Category to Event

**POST** `/events/{id}/categories/{category_id}`

**Requires authorization:** Yes - only user that created by could edit this event.

If everything OK **Response:**

```json
{
  "data": {
    "status": "OK"
  }
}
```

### Remove Category from Event

**DELETE** `/events/{id}/categories/{category_id}`

**Requires authorization:** Yes - only user that created by could edit this event.

If everything OK **Response:**

```json
{
  "data": {
    "status": "OK"
  }
}
```

## Tags

### Create Tag

**POST** `/tags`

**Request:**

```json
{
  "name": "Outdoor"
}
```

**Requires authorization:** Yes

If everything OK **Response:**

```json
{
  "data": {
    "status": "OK"
  }
}
```

### List Tags

**GET** `/tags`

**Requires authorization:** No

**Response:**

```json
{
  "data": [
    {
      "name": "Outdoor"
    },
    /* etc. */
  ]
}
```

### Assign Tag to Event

**POST** `/events/{id}/tags/{tag_id}`

**Requires authorization:** Yes - only user that created by could edit this event.

If everything OK **Response:**

```json
{
  "data": {
    "status": "OK"
  }
}
```

### Remove Tag from Event

**DELETE** `/events/{id}/tags/{tag_id}`

**Requires authorization:** Yes - only user that created by could edit this event.

If everything OK **Response:**

```json
{
  "data": {
    "status": "OK"
  }
}
```
