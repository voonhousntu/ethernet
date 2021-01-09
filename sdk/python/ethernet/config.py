from configparser import ConfigParser
from typing import Dict, Optional


def _init_config(path: str) -> ConfigParser:
    """
    Returns a ConfigParser that reads in a EtherNet configuration file. If the
    file does not exist it will be created.

    Args:
        path (str):
            Optional path to initialize as Feast configuration

    Returns:
        ConfigParser of the Feast configuration file, with defaults preloaded
    """
    # TODO: Implement this method


class Config:
    """
    Maintains and provides access to EtherNet configuration.
    Configuration is stored as key/value pairs. The user can specify options
    through either input arguments to this class, environmental variables, or
    by setting the config in a configuration file
    """

    def __init__(
            self, options: Optional[Dict[str, str]] = None,
            path: Optional[str] = None,
    ):
        """
        Configuration options are returned as follows (higher replaces lower)
        1. Initialized options ("options" argument)
        TODO: 2. Configuration file options (loaded once)
        TODO: 3. Default options (loaded once from memory)

        Args:
            options (Optional[Dict(str, str]):
                 A list of initialized/hardcoded options.

            path (Optional[str]):
                File path to configuration file
        """
        if path:
            config = _init_config(path)

        self._options = {}
        if options and isinstance(options, dict):
            self._options = options

            self._config = config  # type: ConfigParser
            self._path = path  # type: str
