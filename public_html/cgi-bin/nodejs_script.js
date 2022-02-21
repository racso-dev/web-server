#!/usr/bin/node

if (process.env.REQUEST_METHOD === "GET") {
  console.log("GET");
} else {
  console.log(process.env.REQUEST_METHOD);
}
