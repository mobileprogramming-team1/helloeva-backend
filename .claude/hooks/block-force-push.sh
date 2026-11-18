#!/usr/bin/env bash
# PreToolUse(Bash) 훅: git force push 를 차단한다.
input=$(cat)
command=$(echo "$input" | jq -r '.tool_input.command // empty')

if echo "$command" | grep -qE 'git[[:space:]]+push.*(--force(-with-lease)?|[[:space:]]-f([[:space:]]|$))'; then
  echo '{"hookSpecificOutput":{"hookEventName":"PreToolUse","permissionDecision":"deny","permissionDecisionReason":"force push는 hook에 의해 차단됩니다. 정말 필요하면 사용자가 직접 실행하세요."}}'
else
  echo '{}'
fi
