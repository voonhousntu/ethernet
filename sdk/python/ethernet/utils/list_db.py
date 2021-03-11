def list_databases():
    # This function will be teleported, imports need to be locally defined
    from glob import glob
    from os.path import expanduser

    # Define constants
    home = expanduser("~")
    neo4j_db_path = home + "/neo4j/data/databases/"
    dir_names_to_ignore = ["neo4j", "system"]

    # Alter the file names to fit to glob format
    dir_names_to_ignore = [neo4j_db_path + x + "/" for x in dir_names_to_ignore]

    # Get files present in directory
    f = glob(neo4j_db_path + "*/")

    databases = [
        x[:-1].replace(neo4j_db_path, "")
        for x in f
        if x not in dir_names_to_ignore
    ]

    # Sort the strings (Ensure they are sorted if OS does n0t sort them)
    databases.sort()

    return databases
