cat <<EOF | kubectl create -f - 
apiVersion: v1
kind: Secret
metadata:
  name: collect-api-secrets
type: Opaque
data:
  PLAY_SECRET: $(head -c 24 /dev/random | base64 | base64)
EOF
