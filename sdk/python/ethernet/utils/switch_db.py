def switch_db(docker_container_name: str, new_db: str):
    import os
    # print(os.getcwd())
    cmd = f"python3 switch_db.py " \
          f"'{docker_container_name}' '{new_db}'"
    os.popen(cmd).read()
