import os

from httprunner.api import HttpRunner
from httprunner import (__version__, exceptions, loader, logger)

class QHttprunner(HttpRunner):
    '''
    之所以重写HttpRunner 的run方法：是因为HttpRunner的run不支持跑测试用例路径列表，所改写一下
    '''

    def run(self, path_or_tests, dot_env_path=None, mapping=None):
        """ main interface.

        Args:
            path_or_tests:
                str: testcase/testsuite file/foler path
                dict: valid testcase/testsuite data
            dot_env_path (str): specified .env file path.
            mapping (dict): if mapping is specified, it will override variables in config block.

        Returns:
            dict: result summary

        """
        logger.log_info("HttpRunner version: {}".format(__version__))
        if loader.is_test_path(path_or_tests):
            root_path = path_or_tests[0]
            return self.run_paths(root_path, path_or_tests, dot_env_path, mapping)
        elif loader.is_test_content(path_or_tests):
            project_working_directory = path_or_tests.get("project_mapping", {}).get("PWD", os.getcwd())
            loader.init_pwd(project_working_directory)
            return self.run_tests(path_or_tests)
        else:
            raise exceptions.ParamsError("Invalid testcase path or testcases: {}".format(path_or_tests))


    def run_paths(self, root_path, path_or_tests, dot_env_path=None, mapping=None):
        """ run testcase/testsuite file or folder.

        Args:
            path (str): testcase/testsuite file/foler path.
            dot_env_path (str): specified .env file path.
            mapping (dict): if mapping is specified, it will override variables in config block.

        Returns:
            dict: result summary

        """
        # load tests
        self.exception_stage = "load tests"


        tests_mapping = self.load_cases(root_path, path_or_tests, dot_env_path)

        if mapping:
            tests_mapping["project_mapping"]["variables"] = mapping
        logger.get_logger().disabled = False
        return self.run_tests(tests_mapping)


    def load_cases(self, root_path, path_or_tests, dot_env_path=None):
        """ load testcases from file path, extend and merge with api/testcase definitions.

        Args:
            path (str): testcase/testsuite file/foler path.
                path could be in 2 types:
                    - absolute/relative file path
                    - absolute/relative folder path
            dot_env_path (str): specified .env file path

        Returns:
            dict: tests mapping, include project_mapping and testcases.
                  each testcase is corresponding to a file.
                {
                    "project_mapping": {
                        "PWD": "XXXXX",
                        "functions": {},
                        "env": {}
                    },
                    "testcases": [
                        {   # testcase data structure
                            "config": {
                                "name": "desc1",
                                "path": "testcase1_path",
                                "variables": [],                    # optional
                            },
                            "teststeps": [
                                # test data structure
                                {
                                    'name': 'test desc1',
                                    'variables': [],    # optional
                                    'extract': [],      # optional
                                    'validate': [],
                                    'request': {}
                                },
                                test_dict_2   # another test dict
                            ]
                        },
                        testcase_2_dict     # another testcase dict
                    ],
                    "testsuites": [
                        {   # testsuite data structure
                            "config": {},
                            "testcases": {
                                "testcase1": {},
                                "testcase2": {},
                            }
                        },
                        testsuite_2_dict
                    ]
                }

        """

        tests_mapping = {
            "project_mapping": loader.load_project_data(root_path, dot_env_path)
        }

        def __load_file_content(path):
            loaded_content = None
            try:
                loaded_content = loader.buildup.load_test_file(path)
            except exceptions.ApiNotFound as ex:
                logger.log_warning("Invalid api reference in {}: {}".format(path, ex))
            except exceptions.FileFormatError:
                logger.log_warning("Invalid test file format: {}".format(path))

            if not loaded_content:
                pass
            elif loaded_content["type"] == "testsuite":
                tests_mapping.setdefault("testsuites", []).append(loaded_content)
            elif loaded_content["type"] == "testcase":
                tests_mapping.setdefault("testcases", []).append(loaded_content)
            elif loaded_content["type"] == "api":
                tests_mapping.setdefault("apis", []).append(loaded_content)

        for path in path_or_tests:
            if os.path.isdir(path):
                files_list = loader.buildup.load_folder_files(path)
                for path in files_list:
                    __load_file_content(path)

            elif os.path.isfile(path):
                __load_file_content(path)

        return tests_mapping
