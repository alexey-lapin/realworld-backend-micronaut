#!/usr/bin/env bash
# Runs the RealWorld hurl spec file by file, writes a per-file result table to the job
# summary, and fails when any file fails.
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
  echo "| file | result | errors |"
  echo "|---|---|---|"
} >> "$summary"

for file in .realworld-spec/specs/api/hurl/*.hurl; do
  n=$((n + 1))
  # a distinct uid per file keeps usernames and slugs unique across files
  if output=$(hurl --test --continue-on-error --no-color --jobs 1 \
    --variable host="$host" \
    --variable uid="${uid}${n}" \
    "$file" 2>&1); then
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
