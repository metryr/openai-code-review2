curl -X POST \
        -H "Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiIsInNpZ25fdHlwZSI6IlNJR04ifQ.eyJhcGlfa2V5IjoiNjU5ZDkzY2JkOTlkNGYzZDkxMGIzMmY4MWZiNjhhMDkiLCJleHAiOjE3NTY2MzgzNzEwMjcsInRpbWVzdGFtcCI6MTc1NjYzNjU3MTAyOH0.Skaf9V11hWnkZdcjozuGr4J0V9HWsuztTJ-A9B9e9WE" \
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