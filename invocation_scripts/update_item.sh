#!/bin/bash

curl -i 'https://x4f3lpiyowjkc2kgdirhr7a3vq0uyjtd.lambda-url.us-east-1.on.aws/' \
  -X 'PUT' \
  -H 'Content-Type: application/json' \
  -H 'Cache-Control: max-age=0' \
  --data-raw '{"isbn":"2","author":"test author updated","name":"test name updated"}' \
  --compressed