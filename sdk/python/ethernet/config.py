from typing import Dict, Optional


class Config:
    """
    Maintains and provides access to EtherNet configuration
    Configuration is stored as key/value pairs. The user can specify options
    through either input arguments to this class, environmental variables, or
    by setting the config in a configuration file
    """

    def __init__(
            self, options: Optional[Dict[str, str]] = None,
            path: Optional[str] = None,
    ):
        """

        Args:
            options ():
            path ():
        """
        pass
