import os
import sys

NEO4J_CONF_FILE_NAME = "neo4j.conf"

def copy_neo4j_conf(docker_container_name: str):
    cmd = f"docker cp " \
          f"{docker_container_name}:/var/lib/neo4j/conf/{NEO4J_CONF_FILE_NAME} ."
    os.popen(cmd).read()


def write_neo4j_conf(docker_container_name: str):
    cmd = f"docker cp {NEO4J_CONF_FILE_NAME} " \
          f"{docker_container_name}:/var/lib/neo4j/conf/{NEO4J_CONF_FILE_NAME}"
    os.popen(cmd).read()


def restart_docker_container(docker_container_name: str):
    cmd = f"docker stop {docker_container_name} && " \
          f"docker start {docker_container_name}"
    os.popen(cmd).read()


def cleanup():
    os.remove("neo4j.conf")


def modify_conf(new_db_name: str):
    # Read the contents of the config file
    with open(f"{NEO4J_CONF_FILE_NAME}", "r") as f:
        conf = f.read()

    config_identifier = "dbms.default_database="

    conf_lines = []
    # Find the line to change
    for line in conf.split("\n"):
        if config_identifier in line:
            # Change this line regardless if it is commented or not
            mod_line = f"{config_identifier}{new_db_name}".format(
                config_identifier=config_identifier,
                new_db_name=new_db_name
            )
            conf_lines.append(mod_line)
        else:
            conf_lines.append(line)

    # Write the new contents to file
    # Will overwrite old neo4j.conf
    with open(f"{NEO4J_CONF_FILE_NAME}", "w") as f:
        f.writelines("%s\n" % x for x in conf_lines)


if __name__ == "__main__":
    docker_container_name = sys.argv[1]
    new_db_name = sys.argv[2]

    print("start")
    copy_neo4j_conf(docker_container_name)
    modify_conf(new_db_name)
    write_neo4j_conf(docker_container_name)
    restart_docker_container(docker_container_name)
    cleanup()
    print("end")
