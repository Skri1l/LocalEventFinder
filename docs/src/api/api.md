# API 

## Response Format

```json
{
  "datetime": "2026-03-30T12:00:00Z",
  "data": {
    "items": [],        // an array of objects (for lists) or a single object
    "count": 1,         // number of items in the response (for a list)
    "total": 1          // total number of items (for a list)
  },
  "error": null          // null if success, object if error
}
```

### Success Example

```json
{
  "datetime": "2026-03-30T12:00:00Z",
  "data": {
    "user": {
      "id": "123",
      "username": "john_doe",
      "email": "john@example.com",
      "age": 25,
      "avatar_url": "https://example.com/avatar.png"
    }
  },
  "error": null
}
```

### Error Example

```json
{
  "datetime": "2026-03-30T12:00:00Z",
  "data": null,
  "error": {
    "code": 404,
    "message": "User not found"
  }
}
```

## Authentication

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
  "datetime": "...",
  "data": {
    "access_token": "jwt",
    "refresh_token": "jwt",
    "expires_in": 3600
  }
}
```

### Auth Header

```
Authorization: Bearer <access_token>
```

## Users

### Register User

**POST** `/users`

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

### Get User

**GET** `/users/{id}`

### Update User (Partial)

**PATCH** `/users/{id}`

**Request Example:**

```json
{
  "username": "new_name"
}
```

### Delete User

**DELETE** `/users/{id}`

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

> `created_by` tooks from tocken

### Get Event

**GET** `/events/{id}`

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

### Update Event

**PATCH** `/events/{id}`

### Delete Event

**DELETE** `/events/{id}`

### Event Status

```json
"status": "draft | active | cancelled | finished"
```

## Event Participants

### Join Event

**POST** `/events/{id}/participants`

> The user is identified by the token, `user_id` is not transmitted.

### Leave Event

**DELETE** `/events/{id}/participants/me`

### Get Participants

**GET** `/events/{id}/participants`

## 🏷 Categories

### Create Category

**POST** `/categories`

**Request:**

```json
{
  "name": "Sports"
}
```

### List Categories

**GET** `/categories`

### Assign Category to Event

**POST** `/events/{id}/categories/{category_id}`

### Remove Category from Event

**DELETE** `/events/{id}/categories/{category_id}`

## Tags

### Create Tag

**POST** `/tags`

**Request:**

```json
{
  "name": "Outdoor"
}
```

### List Tags

**GET** `/tags`

### Assign Tag to Event

**POST** `/events/{id}/tags/{tag_id}`

### Remove Tag from Event

**DELETE** `/events/{id}/tags/{tag_id}`
