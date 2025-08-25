#!/bin/bash
curl -X POST http://localhost:8080/meetings \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Team Sync",
    "description": "Weekly team meeting",
    "startTime": "2025-08-26T09:00:00",
    "endTime": "2025-08-26T10:00:00"
  }'

