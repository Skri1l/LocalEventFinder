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
    "username": "john_doe",
    "email": "john@example.com",
    "password": "StrongPassword123!",
    "avatar_url": "https://example.com/avatar.png",
    "age": 25
  }
}
```

For list of entities:
```json
{
  "data": [
    {
      "username": "john_doe",
      "email": "john@example.com",
      "password": "StrongPassword123!",
      "avatar_url": "https://example.com/avatar.png",
      "age": 25
    },
    {
      "username": "john_doe2",
      "email": "john2@example.com",
      "password": "StrongPassword123!",
      "avatar_url": "https://example.com/avatar.png",
      "age": 22
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

If everything OK **Response:**

```json
{
  "OK"
}
```

## Authentication

### Auth Header

```
Authorization: Bearer <access_token>
```

### Login

**POST** `/auth/login`

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

### Register User

**POST** `/auth/register`

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

## Users

### Get User

**GET** `/users/{id}`

**Requires authorization:** Yes

### Update User (Partial)

**PATCH** `/users/{id}`

**Request Example:**

```json
{
  "username": "new_name"
}
```

**Requires authorization:** Yes - A user can only edit their own profile.

### Delete User

**DELETE** `/users/{id}`

**Requires authorization:** Yes - A user can only delete their own profile.

## Events

### Create Event

**POST** `/events`

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
  "status": "active"
}
```

**Requires authorization:** Yes

> `created_by` tooks from tocken

### Get Event

**GET** `/events/{id}`

**Requires authorization:** Yes

### List Events

**GET** `/events`

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

**Requires authorization:** Yes

### Update Event

**PATCH** `/events/{id}`

**Requires authorization:** Yes - only user that created by could edit this event.

### Delete Event

**DELETE** `/events/{id}`

**Requires authorization:** Yes - only user that created by could delete this event.

## Event Participants

### Join Event

**POST** `/events/{id}/participants`

> The user is identified by the token (from header), `user_id` is not transmitted.

**Requires authorization:** Yes

### Leave Event

**DELETE** `/events/{id}/participants/me`

**Requires authorization:** Yes

### Get Participants

**GET** `/events/{id}/participants`

**Requires authorization:** Yes

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

### List Categories

**GET** `/categories`

**Requires authorization:** No

### Assign Category to Event

**POST** `/events/{id}/categories/{category_id}`

**Requires authorization:** Yes - only user that created by could edit this event.

### Remove Category from Event

**DELETE** `/events/{id}/categories/{category_id}`

**Requires authorization:** Yes - only user that created by could edit this event.

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

### List Tags

**GET** `/tags`

**Requires authorization:** No

### Assign Tag to Event

**POST** `/events/{id}/tags/{tag_id}`

**Requires authorization:** Yes - only user that created by could edit this event.

### Remove Tag from Event

**DELETE** `/events/{id}/tags/{tag_id}`

**Requires authorization:** Yes - only user that created by could edit this event.
