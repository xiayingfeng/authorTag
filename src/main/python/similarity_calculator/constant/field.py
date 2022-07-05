# do match, calculate precision and recall
# true positive:    cos_sim >= threshold && target descr == ground truth
TP_count: int = 0

# false positive:
#   cos_sim >= threshold && target descr != ground truth
#   cos_sim >= threshold for mismatched commits
FP_count: int = 0

# false negative:   cos_sim < threshold && target descr == ground truth
FN_count: int = 0

# true negative:
#   cos_sim <  threshold && target descr != ground truth
#   cos_sim >= threshold for mismatched commits
TN_count: int = 0

result_list = []
FN_list = []
FP_list = []

result_dict = {
    "threshold": "",
    "precision": "",
    "recall": "",
    "list size": "",
    "result list": ""
}