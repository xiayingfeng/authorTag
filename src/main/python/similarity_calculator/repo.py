from ast import If
from pair import *


class repo:
    repo_name: str
    pair_list: list[pair]
    mismatch_commits_list: list
    is_descr_list_inited: bool
    truth_list: list[str]

    def __init__(self, repo_name: str, pair_list: list, mis_list: list):
        self.repo_name = repo_name
        self.pair_list = pair_list
        self.mismatch_commits_list = mis_list
        self.descr_list = []
        self.truth_list = []
        self.is_descr_list_inited = False

    def init_descr_lists(self):
        for item in self.pair_list:
            self.descr_list.append(item.description)
        self.is_descr_list_inited = True

    def get_descr_list(self):
        if self.is_descr_list_inited == False:
            self.init_descr_lists()
        return self.descr_list

    def get_commit_messsage_list(self):
        commit_messsage_list = []
        for item in self.pair_list:
            commit_messsage_list += item.get_commit_message_list()
            self.truth_list += item.fill_ground_truth_list()
        return commit_messsage_list

    def get_mismatched_commit_message_list(self):
        commit_message_list = []
        for item in self.mismatch_commits_list:
            commit_message_list.append(item['commit message'])
        return commit_message_list
