from ast import keyword
from re import T


class commit:
    commit_id: str
    type: str
    commit_message: str
    keywords: list

    def __init__(self, commit_id: str, type: str, keywords: list, commit_message: str = ''):
        self.commit_id = commit_id
        self.type = type
        self.keywords = keywords
        self.commit_message = commit_message
