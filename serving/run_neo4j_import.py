import os
import sys
import rpyc

def run_neo4j_import(host: str, port: str, container_name: str, command: str):
  # Connect to rpyc
  conn = rpyc.classic.connect(host=host, port=port)

  docker_cmd_prefix = "docker exec -it {} /var/lib/neo4j/bin/neo4j-admin ".format(container_name)
  # Escape double quotes
  command = command.replace('"','\\"')

  # Execute the shell command from python env
  conn.execute("import os")
  std_out = conn.eval("os.popen(\"{}\").read()".format(docker_cmd_prefix + command))
  print(std_out)

  # Close the rpyc connection
  conn.close()

if __name__ == "__main__":
  # First argument is the python file name at index 0
  host = sys.argv[1]
  port = sys.argv[2]
  container_name = sys.argv[3]
  cmd = sys.argv[4]

  run_neo4j_import(
    host=host,
    port=port,
    command=cmd,
    container_name=container_name
  )
