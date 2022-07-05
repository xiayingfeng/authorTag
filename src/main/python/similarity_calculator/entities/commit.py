class Commit:
    commit_id: str
    commit_type: str
    commit_message: str
    keywords: list

    def __init__(self, commit_id: str, commit_type: str, keywords: list, commit_message: str = ''):
        self.commit_id = commit_id
        self.commit_type = commit_type
        self.keywords = keywords
        self.commit_message = commit_message
