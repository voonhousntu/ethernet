import sys
import os

def submit_command(host: str, port: str, command: str):
    # Connect to rpyc
    conn = rpyc.classic.connect(host=host, port=port)

    # Execute the shell command from python env
    conn.execute("import os")
    std_out = conn.eval("os.popen(\"{}\").read()".format(command))

    # Close the rpyc connection
    conn.close()
    return std_out

if __name__ == "__main__":
    # First argument is the python file name at index 0
    host = sys.argv[1]
    port = sys.argv[2]
    cmd = sys.argv[3]
    std_out = submit_command(host, port, cmd)
    print(std_out)
