#!/usr/bin/env bash
# Maven wrapper script with support for 'run' shortcut (maps to spring-boot:run)

set -e

# Find mvn binary
if command -v mvn &> /dev/null; then
    MVN_CMD="mvn"
elif [ -x "/usr/share/maven/bin/mvn" ]; then
    MVN_CMD="/usr/share/maven/bin/mvn"
else
    echo "Erro: Maven não encontrado. Instale o Maven ou configure-o no PATH." >&2
    exit 1
fi

if [ "$1" = "run" ]; then
    shift
    exec "$MVN_CMD" spring-boot:run "$@"
fi

exec "$MVN_CMD" "$@"
