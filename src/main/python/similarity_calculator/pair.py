from commit import *


class pair:
    pair_id: str
    description: str
    commit_list: list[commit]

    def __init__(self, pair_id: str, descr: str, commit_list: list):
        self.pair_id = pair_id
        self.description = descr
        self.commit_list = commit_list

    def get_commit_message_list(self):
        message_list = []
        for item in self.commit_list:
            message_list.append(item.commit_message)
        return message_list

    def fill_ground_truth_list(self):
        truth_list = []
        for i in range(0, len(self.commit_list)):
            truth_list.append(self.description)
        return truth_list
