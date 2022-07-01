import re
from constant import *
from json_utils import *


mismatch_out_file: str = "F:\\SelfFileBackUp\\Term\\Lab\\License_Reading\\authorTag\\src\\main\\python\\data\\mismatch_out.json"
f = open(file=answer_file, encoding='utf-8')
json_dict = json.load(f)

for tmp_repo in json_dict['data']:
    repo_name: str = tmp_repo['repo']
    descr_txt_file: str = data_dir + \
        repo_name.replace('/', '__fdse__') + ".txt"
    descr_content: str = open(
        descr_txt_file, encoding="utf-8").read()

    mis_commit_list = []
    for mis_commit_id in tmp_repo['mismatch commits']:
        regex: str = 'commit ' + mis_commit_id + \
            '+? ' + '\d{10} -----sp :: (.*)\n'
        tmp_debug = re.search(regex, descr_content)
        if tmp_debug is None:
            continue
        res = re.search(regex, descr_content).group()

        index: int = res.find('::')
        commit_message: str = res[index + 3:].strip()

        mis_commit_dict = {
            "commit id": mis_commit_id,
            "commit message": commit_message
        }
        mis_commit_list.append(mis_commit_dict)

    tmp_repo['mismatch commits'] = mis_commit_list
write_json(json_dict, mismatch_out_file)
