# API

- [Responce Template](#responce-template)
- [Users](#users)

## Responce Template

```json
{
  "datetime": "response date time",
  "count": 0,
  "data": {}
}
```

- datetime - timestamp of the date and time of the response,
- count - amount of data,
- data - useful information.

## Users

Mapping by `/users`

### Register User

**POST** `/users/register`

Request:
```json
{ 
    "username": "john_doe", 
    "email": "john@example.com", 
    "password": "password123", 
    "avatar_url": "https://example.com/avatar.png", 
    "age": 25 
}
```

Responce:
```json
{ 
    "id": "uuid", 
    "username": "john_doe", 
    "email": "john@example.com", 
    "avatar_url": "...", 
    "age": 25, 
    "created_at": "timestamp" 
}
```

### Login user

**POST** `/users/login`

```json
{
  "email": "john@example.com",
  "password": "password123"
}
```

### Get User

**GET** `/users/{id}` 

Responce:
```json
{ 
    "id": "uuid", 
    "username": "john_doe", 
    "email": "john@example.com", 
    "avatar_url": "...", 
    "age": 25, 
    "created_at": "timestamp" 
}
```

### Update User

**PUT** `/users/{id}` 

### Delete User

**DELETE** `/users/{id}` 

## Events

Mapping by `/events`

### Event create

**POST** `/events`

Request:
```json
{ 
    "title": "Music Festival", 
    "description": "Outdoor event", 
    "latitude": 54.6872, 
    "longitude": 25.2797, 
    "country": "Lithuania", 
    "city": "Vilnius", 
    "start_time": "2026-06-01T10:00:00Z", 
    "end_time": "2026-06-01T18:00:00Z", 
    "max_participants": 100, 
    "age_restriction": 18,
    "image_url": "...", 
    "created_by": "user_uuid" 
}
```

### Get Event

**GET** `/events/{id}`

### List Event

**GET** `/events`

Query Parameters:
- `city`
- `country`
- `category_id`
- `tag_id`
- `date_from`
- `date_to`

### Update Event

**PUT** `/events/{id}`

### Delete Event

**DELETE** `/events/{id}`

## Event Participants

### Join Event

**POST** `/events/{id}/join`

Request:
```json
{ 
    "user_id": "uuid" 
}
```

### Leave Event

**POST** `/events/{id}/leave`

Request:
```json
{ 
    "user_id": "uuid" 
}
```

### Get Event Participants

**GET** `/events/{id}/participants`

## Categories

Mapping by  `/categories`

### Create Category

**POST** `/categories`

```json
{
  "name": "Sports"
}
```

### Get Categories

**GET** `/categories`

### Assign Category to Event

**POST** `/events/{id}/categories`

```json
{
  "category_id": "uuid"
}
```

### Remove Category from Event

**DELETE** `/events/{id}/categories/{category_id}`

## Tags

Mapping by `/tags`

### Create Tag

**POST** `/tags`

```json
{
  "name": "Outdoor"
}
```

### Get Tags

**GET** `/tags`

### Assign Tag to Event

**POST** `/events/{id}/tags`

```json
{
  "tag_id": "uuid"
}
```

### Remove Tag from Event

**DELETE** `/events/{id}/tags/{tag_id}`