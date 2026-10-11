#!/usr/bin/env bash
# Runs the RealWorld hurl spec file by file and writes a per-file result table to the
# job summary, so partial conformance stays visible while the suite is not gating.
# Usage: hurl-report.sh <host> <uid>
set -uo pipefail

host="$1"
uid="$2"
summary="${GITHUB_STEP_SUMMARY:-/dev/stdout}"
failed=0
n=0

{
  echo "### RealWorld API spec"
  echo
  echo "| file | result | failed asserts |"
  echo "|---|---|---|"
} >> "$summary"

for file in .realworld-spec/specs/api/hurl/*.hurl; do
  n=$((n + 1))
  # a distinct uid per file keeps usernames and slugs unique across files
  output=$(hurl --test --continue-on-error --jobs 1 \
    --variable host="$host" \
    --variable uid="${uid}${n}" \
    "$file" 2>&1)
  if [ $? -eq 0 ]; then
    result="pass"
  else
    result="fail"
    failed=$((failed + 1))
  fi
  errors=$(grep -c '^error:' <<< "$output")
  echo "| $(basename "$file") | $result | $errors |" >> "$summary"
  [ "$result" = "fail" ] && echo "$output"
done

echo >> "$summary"
echo "$failed of $n files failed"
[ "$failed" -eq 0 ]
