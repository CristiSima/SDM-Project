#!/usr/bin/env python

import asyncio
import uuid
import os
from datetime import datetime
from websockets.asyncio.server import serve, basic_auth, ServerConnection
from json import loads, dump

DUMP_DIR = "DATA"

async def echo(websocket: ServerConnection):
    # Extract client address details
    host, port = websocket.remote_address
    client_prefix = f"{host}_{port}"
    
    print(f"Connected to {host}:{port}")
    
    async for message in websocket:
        print(f"Got msg: {message}")

        msg = loads(message)

        # Generate timestamp and UUID for uniqueness
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S_%f")
        unique_id = uuid.uuid4()
        client_type = msg["type"]
        filename = f"{client_prefix}_{client_type}_{timestamp}.json"
        
        # Save the message to a file
        try:
            # Check if message is bytes or string and open accordingly
            with open(DUMP_DIR + "/" + filename, "w", encoding="utf-8") as f:
                # dump(msg["data"], f)
                f.write(msg["data"])
            print(f"Saved to {filename}")
        except Exception as e:
            print(f"Error saving message: {e}")

        # Echo the message back
        await websocket.send(message)

async def main():
    async with serve(echo, "0.0.0.0", 8765,
                process_request=basic_auth(
                    realm="my dev server",
                    credentials=("hello", "iloveyou"),
                )
            ) as server:
        await server.serve_forever()

if __name__ == "__main__":
    asyncio.run(main())