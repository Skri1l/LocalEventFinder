# DataBase description

## Tables Description

### users

| Field Name    | Type      | Description            |
| ------------- | --------- | ---------------------- |
| id            | UUID      | Unique user identifier |
| username      | VARCHAR   | User nickname          |
| email         | VARCHAR   | User email address     |
| password_hash | VARCHAR   | Hashed password        |
| avatar_url    | VARCHAR   | Profile image URL      |
| age           | INTEGER   | User age               |
| created_at    | TIMESTAMP | Account creation date  |

### events

| Field Name       | Type      | Description                    |
| ---------------- | --------- | ------------------------------ |
| id               | UUID      | Unique event identifier        |
| title            | VARCHAR   | Event title                    |
| description      | TEXT      | Event description              |
| latitude         | DECIMAL   | Location latitude              |
| longitude        | DECIMAL   | Location longitude             |
| country          | VARCHAR   | Country                        |
| city             | VARCHAR   | City                           |
| start_time       | TIMESTAMP | Event start time               |
| end_time         | TIMESTAMP | Event end time                 |
| max_participants | INTEGER   | Maximum number of participants |
| age_restriction  | INTEGER   | Minimum required age           |
| image_url        | VARCHAR   | Event image URL                |
| created_by       | UUID (FK) | Creator (user id)              |
| created_at       | TIMESTAMP | Creation date                  |

### event_participants (Many-to-Many)

| Field Name | Type      | Description         |
| ---------- | --------- | ------------------- |
| user_id    | UUID (FK) | Reference to users  |
| event_id   | UUID (FK) | Reference to events |
| joined_at  | TIMESTAMP | When user joined    |

### categories

| Field Name | Type    | Description   |
| ---------- | ------- | ------------- |
| id         | UUID    | Category ID   |
| name       | VARCHAR | Category name |


### event_categories

| Field Name  | Type      | Description        |
| ----------- | --------- | ------------------ |
| event_id    | UUID (FK) | Event reference    |
| category_id | UUID (FK) | Category reference |

### tags

| Field Name | Type    | Description |
| ---------- | ------- | ----------- |
| id         | UUID    | Tag ID      |
| name       | VARCHAR | Tag name    |

### event_tags

| Field Name | Type      | Description     |
| ---------- | --------- | --------------- |
| event_id   | UUID (FK) | Event reference |
| tag_id     | UUID (FK) | Tag reference   |

## Diagram

![DB-diagram-image](../../assets/database-design/diagram.png)

## DBML Format

```dbml
// Use DBML to define your database structure
// Docs: https://dbml.dbdiagram.io/docs
Table users {
  id uuid [pk]
  username varchar
  email varchar [unique]
  password_hash varchar
  avatar_url varchar
  age int
  created_at timestamp
}

Table events {
  id uuid [pk]
  title varchar
  description text
  latitude decimal
  longitude decimal
  country varchar
  city varchar
  start_time timestamp
  end_time timestamp
  max_participants int
  age_restriction int
  image_url varchar
  created_by uuid [ref: > users.id]
  created_at timestamp
}

Table event_participants {
  user_id uuid [ref: > users.id]
  event_id uuid [ref: > events.id]
  joined_at timestamp

  indexes {
    (user_id, event_id) [pk]
  }
}

Table categories {
  id uuid [pk]
  name varchar
}

Table event_categories {
  event_id uuid [ref: > events.id]
  category_id uuid [ref: > categories.id]

  indexes {
    (event_id, category_id) [pk]
  }
}

Table tags {
  id uuid [pk]
  name varchar
}

Table event_tags {
  event_id uuid [ref: > events.id]
  tag_id uuid [ref: > tags.id]

  indexes {
    (event_id, tag_id) [pk]
  }
}
``` 
