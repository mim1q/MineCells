import os
from os import listdir
from os.path import isfile, join


def autolang(entries: [str], prefix: str) -> [str]:
    result = []
    for entry in entries:
        result.append("\"" + prefix + entry + "\": \"" + snake_case_to_capitalized(entry) + "\"")
    return result


def get_entries_from_directory(directory: str) -> [str]:
    files = [f for f in listdir(directory) if isfile(join(directory, f))]
    return [f.replace(".json", "") for f in files]


def write_autolang_file(output_path: str, entries: [str]):
    output_file = output_path + "\\autolang.json5"

    text = "// COPY THESE ENTRIES TO YOU MAIN LANG FILE \n{\n"
    text += "".join(["\t" + e + ",\n" for e in entries])
    text = text[:-1] + "\n}"

    os.makedirs(os.path.dirname(output_file), exist_ok=True)
    with open(output_file, "w") as save_file:
        save_file.write(text)


def snake_case_to_capitalized(text: str) -> str:
    return text.title().replace("_", " ")
