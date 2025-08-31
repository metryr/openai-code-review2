curl -X POST \
        -H "Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiIsInNpZ25fdHlwZSI6IlNJR04ifQ.eyJhcGlfa2V5IjoiZWNjYmVmNzg4ZGMxNGU2MGFiN2MzMTUwY2Y5NmFhYTUiLCJleHAiOjE3NTY2MzUxNzczMDMsInRpbWVzdGFtcCI6MTc1NjYzMzM3NzM0Nn0.ijkdlQ0Ehl11OQXZTDftx2WrspAFeHE8-7JtlcEvGmE" \
        -H "Content-Type: application/json" \
        -H "User-Agent: Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)" \
        -d '{
          "model":"glm-4",
          "stream": "true",
          "messages": [
              {
                  "role": "user",
                  "content": "1+1"
              }
          ]
        }' \
  https://open.bigmodel.cn/api/paas/v4/chat/completions