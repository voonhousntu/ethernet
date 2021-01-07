from typing import Dict, Optional

from ethernet.config import Config


class Client:
    """
    EtherNet Client: Used for creating, managing, and retrieving Ethereum
    graphs.
    """

    def __init__(self, options: Optional[Dict[str, str]], **kwargs):
        """
        The EtherNet Client should be initialized with at lease one service
        url.

        Commonly used options or arguments include:
            core_url: EtherNet core URL. Used to manage Ethereum graphs.

        Args:
            options:
                Configuration options to initialize client with

            **kwargs:
                Additional keyword arguments that will be used as
                configuration options along with "options"
        """

        if options is None:
            options = dict()
        self._config = Config(options={**options, **kwargs})
        self._config = Config(options={**options, **kwargs})
