from node:18
workdir /app
copy index.js .
cmd ["node", "index.js"]