import re
from traceback import print_tb


# def is_forced_match(commit_message: str, description: str):
#     id_in_commit = re.search(r'#\d+', commit_message).group()
#     id_in_descr = re.search(r'#\d+', description).group()
#     if id_in_commit == None or id_in_descr == None:
#         return False
#     return id_in_commit == id_in_descr

# print(is_forced_match("#12", ""))

start_git = '(https://github.com'
end_git = ')'


def trip_git_url(descr: str):
    ret_descr = descr
    start_index = descr.find(start_git)
    while start_index != -1:
        end_index = descr.find(end_git, start_index)
        substring = descr[start_index: end_index]
        ret_descr.replace(substring, '')

    return ret_descr


raw_descr: str = '* [#159](https://github.com/lerna/lerna-changelog/pull/159) Adjust `.npmignore` file ([@Turbo87](https://github.com/Turbo87))'
test_str: str = trip_git_url(raw_descr)
print(test_str)
