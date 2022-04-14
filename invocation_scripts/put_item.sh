#!/bin/bash

curl -i 'https://gs57yqmltxuo5nc4fndfaz5ska0bmodt.lambda-url.us-east-1.on.aws/' \
  -X 'POST' \
  -H 'Content-Type: application/json' \
  -H 'Cache-Control: max-age=0' \
  --data-raw '{"isbn":"2","author":"test author created","name":"test name created"}' \
  --compressed