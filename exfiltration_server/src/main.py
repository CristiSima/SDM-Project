#!/usr/bin/env python

import asyncio
from websockets.asyncio.server import serve, basic_auth, ServerConnection

async def echo(websocket: ServerConnection):
    websocket.logger.info(websocket.transport)
    async for message in websocket:
        await websocket.send(message)

async def main():
    async with serve(echo, "0.0.0.0", 8765,
                process_request=basic_auth(
                    realm="my dev server",
                    credentials=("hello", "iloveyou"),
                )
            ) as server:
        await server.serve_forever()

asyncio.run(main())