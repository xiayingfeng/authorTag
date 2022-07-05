from similarity_calculator.entities.pair import Pair


class Repo:
    repo_name: str
    pair_list: list[Pair]
    truth_list: list[str]
    mismatch_commits_list: list

    descr_list: list[str]
    matched_commit_message_list: list
    mismatch_commits_message_list: list

    tfidf_descr_list: list
    tfidf_matched_commits_list: list
    tfidf_mismatch_commits_list: list

    def __init__(self, repo_name: str, pair_list: list, mis_list: list):
        self.repo_name = repo_name
        self.pair_list = pair_list
        self.truth_list = []
        self.mismatch_commits_list = mis_list

        self.descr_list = []
        self.matched_commit_message_list = []
        self.mismatch_commits_message_list = []

        self.tfidf_descr_list = []
        self.tfidf_matched_commits_list = []
        self.tfidf_mismatch_commits_list = []

    def get_descr_list(self):
        if len(self.descr_list) == 0:
            for item in self.pair_list:
                self.descr_list.append(item.description)
        return self.descr_list

    def get_matched_commit_message_list(self):
        if len(self.matched_commit_message_list) == 0:
            for item in self.pair_list:
                self.matched_commit_message_list += item.get_commit_message_list()
                self.truth_list += item.fill_ground_truth_list()
        return self.matched_commit_message_list

    def get_mismatched_commit_message_list(self):
        if len(self.mismatch_commits_message_list) == 0:
            for item in self.mismatch_commits_list:
                self.mismatch_commits_message_list.append(item['commit message'])
        return self.mismatch_commits_message_list

    def get_sum_len(self):
        len_0 = len(self.get_descr_list())
        len_1 = len(self.get_matched_commit_message_list())
        len_2 = len(self.get_mismatched_commit_message_list())
        sum_len = len_0 + len_1 + len_2
        return sum_len

    def set_tfidf_list(self, tfidf: list):
        descr_list_len: int = len(self.get_descr_list())
        match_list_len: int = len(self.get_matched_commit_message_list())
        mismatch_list_len: int = len(self.get_mismatched_commit_message_list())

        self.tfidf_descr_list = tfidf[:descr_list_len]
        self.tfidf_matched_commits_list = tfidf[descr_list_len:match_list_len + descr_list_len]
        self.tfidf_mismatch_commits_list = tfidf[-mismatch_list_len:]
