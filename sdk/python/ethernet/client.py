from typing import List

import grpc
import rpyc

import ethernet.core.ConfigService_pb2 as config_pb2
import ethernet.core.ConfigService_pb2_grpc  as config_pb2_grpc
import ethernet.core.CoreService_pb2 as core_pb2
import ethernet.core.CoreService_pb2_grpc as core_pb2_grpc
import ethernet.utils.switch_db as sdb_util
from ethernet.utils.list_db import list_databases


class Client:
    """
    Ethernet Client: Used for creating, managing, and retrieving Ethereum
    graphs.
    """

    def __init__(self, core_host: str, core_grpc_port: int,
                 core_http_port: int):
        """
        The Ethernet Client should be initialized with at lease one service
        url.

        Args:
            core_host:
                Ethernet core URL. Used to manage Ethereum ETL jobs and
                managing dependencies.

            core_grpc_port:
                Ethernet core grpc port. This port will used to interface
                with Ethernet exposed gRPC services.

            core_http_port:
                Ethernet core http port. This port will be used to interface
                with Ethernet exposed http services.
        """
        self.core_host = core_host
        self.core_grpc_port = core_grpc_port
        self.core_http_port = core_http_port
        self.grpc_channel = "{core_host}:{core_grpc_port}".format(
            core_host=self.core_host,
            core_grpc_port=self.core_grpc_port
        )

        # Initialise relevant configs
        self.neo4j_config = self.get_neo4j_config()
        self.serving_config = self.get_serving_config()

    def get_neo4j_config(self) -> config_pb2.GetNeo4jServingConfigResponse:
        with grpc.insecure_channel(self.grpc_channel) as channel:
            stub = config_pb2_grpc.ConfigServiceStub(channel=channel)
            neo4j_conf_req = config_pb2.GetNeo4jServingConfigRequest()
            neo4j_conf_res = stub.GetNeo4jServingConfig(neo4j_conf_req)
            return neo4j_conf_res

    def get_serving_config(self) -> config_pb2.GetServingConfigResponse:
        with grpc.insecure_channel(self.grpc_channel) as channel:
            stub = config_pb2_grpc.ConfigServiceStub(channel=channel)
            serving_conf_req = config_pb2.GetServingConfigRequest()
            serving_conf_res = stub.GetServingConfig(serving_conf_req)
            return serving_conf_res

    def get_databases(self) -> List[str]:
        # Connect to rpyc
        conn = rpyc.classic.connect(
            host=self.serving_config.rpyc_host,
            port=self.serving_config.rpyc_port
        )
        return conn.teleport(list_databases)()

    def switch_database(self, new_db: str):
        # Connect to rpyc
        conn = rpyc.classic.connect(
            host=self.serving_config.rpyc_host,
            port=self.serving_config.rpyc_port
        )

        # Ensure that the requested db change exists
        dbs = conn.teleport(list_databases)()
        if new_db in dbs:
            # rpyc does not support for-loops
            # Have to run a remote python script
            teleport_fn = conn.teleport(sdb_util.switch_db)
            teleport_fn(
                docker_container_name=self.serving_config.docker_container_name,
                new_db=new_db
            )
        else:
            print(f"Unable to find `{new_db}` in {dbs}")

        conn.close()

    # def switch_database(self, new_db: str):
    #     # Connect to rpyc
    #     conn = rpyc.classic.connect(
    #         host=self.serving_config.rpyc_host,
    #         port=self.serving_config.rpyc_port
    #     )
    #     dbs = conn.teleport(list_databases)()
    #     if new_db in dbs:
    #         copy_neo4j_conf = conn.teleport(sdb_util.copy_neo4j_conf)
    #         copy_neo4j_conf(self.serving_config.docker_container_name)
    #
    #         modify_conf = conn.teleport(sdb_util.modify_conf)
    #         modify_conf(new_db)
    #
    #         write_neo4j_conf = conn.teleport(sdb_util.write_neo4j_conf)
    #         write_neo4j_conf(self.serving_config.docker_container_name)
    #
    #         restart_docker_container = conn.teleport(
    #             sdb_util.restart_docker_container)
    #         restart_docker_container(
    #         self.serving_config.docker_container_name)
    #
    #         cleanup = conn.teleport(sdb_util.cleanup)
    #         cleanup()
    #     else:
    #         print(f"Unable to find `{new_db}` in {dbs}")
    #         conn.close()

    def create_token_transfers_graph(
            self, start_block_number: int, end_block_number: int
    ):
        # Create update request
        request = core_pb2.UpdateRequest(
            start_block_number=start_block_number,
            end_block_number=end_block_number
        )

        with grpc.insecure_channel(self.grpc_channel) as channel:
            stub = core_pb2_grpc.CoreServiceStub(channel=channel)
            response = stub.UpdateTokenTransfers(request)
            return response

    def create_traces_graph(
            self, start_block_number: int, end_block_number: int
    ):
        # Create update request
        request = core_pb2.UpdateRequest(
            start_block_number=start_block_number,
            end_block_number=end_block_number
        )

        with grpc.insecure_channel(self.grpc_channel) as channel:
            stub = core_pb2_grpc.CoreServiceStub(channel=channel)
            response = stub.UpdateTraces(request)
            return response

    def create_transactions_graph(
            self, start_block_number: int, end_block_number: int
    ):
        # Create update request
        request = core_pb2.UpdateRequest(
            start_block_number=start_block_number,
            end_block_number=end_block_number
        )

        with grpc.insecure_channel(self.grpc_channel) as channel:
            stub = core_pb2_grpc.CoreServiceStub(channel=channel)
            response = stub.UpdateTransactions(request)
            return response
