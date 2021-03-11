from typing import Dict, Optional

from ethernet.config import Config
from example_pb2_grpc import ExampleServiceStub


class Client:
    """
    EtherNet Client: Used for creating, managing, and retrieving Ethereum
    graphs.
    """

    def __init__(self, core_host: str, core_grpc_port: int, core_http_port: int):
        """
        The Ethernet Client should be initialized with at lease one service
        url.

        Args:
            core_host:
                Ethernet core URL. Used to manage Ethereum ETL jobs and managing dependencies.

            core_grpc_port:
                Ethernet core grpc port. This port will used to interface with Ethernet exposed gRPC services.

            core_http_port:
                Ethernet core http port. This port will be used to interface with Ethernet exposed http services.

            **kwargs:
                Additional keyword arguments that will be used as
                configuration options along with "options"
        """
        self.core_host = core_host
        self.core_grpc_port = core_grpc_port
        self.core_http_port = core_http_port
