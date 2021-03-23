/* Copyright 2020 Luzhuo. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.luzhuo.ossdemo;

/**
 * Description:
 *
 * @Author: Luzhuo
 * @Creation Date: 2020/5/6 14:55
 * @Copyright: Copyright 2020 Luzhuo. All rights reserved.
 **/
public class STSBean {

    /**
     * msg : success
     * code : 200
     * data : {"StatusCode":"200","AccessKeyId":"STS.NUXEo9JacnYzDPQHgDjkSpY8Q","AccessKeySecret":"FuNjsUbgJRstGjBSNmSMET82GDTwhX1H6rSudCPxGRWR","SecurityToken":"CAIS1wJ1q6Ft5B2yfSjIr5btDtWNp75C2ZuRRnbgrGcRZuR/n5yTszz2IH9EfnJoCO4YsPg/mm1Z7PgTlqRdaqRlAGjlQ+Y1x6xvqcFXuyU3/Z7b16cNrbH4M6j6aXeirgW7AYjQSNfaZY2CCTTtnTNyxr3XbCirW0ffX7SClZ9gaKZQPG6/diEUINZNOixoqsIRKWCzUPG2KUzFj3b3BkhlsRYe7GRk8vaH39G74BjTh0GA8o1znYnqJYW+ZMRBJYp2V8zPvNZ7ba3cyiVdmUQoha59l/5D4iyV/IPfUUhM+BWLNeWR9cZ0a00bGaExAPxDt+Ou16866P3OjIT6zRkIOvpOcVyGGNn/kZWbQrr4ZopkJemhARmXjIDTbKuSmhg/fHcWODlNf9ccMXJqAXQuMGqFevX8oAGUPFn/G/bYgftogIARykTy9N+bOkOPTrOWwb6lljT3s+lCGoABibOwZ11hdUVOhEiqQ6DvB1drDYgA+hThI/yu6Bn+pyHkNu3kLFkeuYdDzURAj89cVIRbO8apXn8iCu5aVpIZKfVlUG9UTP7pM+AlnRpH5cqrvgK2nk1giOPf9yeYHefGDHtZwH7s//dq1e28lW95Os9RCVnkPOmhwmKPEQ0YNEU=","Expiration":"2020-05-06T07:38:36Z"}
     */

    public String msg;
    public int code;
    public DataBean data;

    public static class DataBean {
        /**
         * AccessKeyId : STS.NUXEo9JacnYzDPQHgDjkSpY8Q
         * AccessKeySecret : FuNjsUbgJRstGjBSNmSMET82GDTwhX1H6rSudCPxGRWR
         * SecurityToken : CAIS1wJ1q6Ft5B2yfSjIr5btDtWNp75C2ZuRRnbgrGcRZuR/n5yTszz2IH9EfnJoCO4YsPg/mm1Z7PgTlqRdaqRlAGjlQ+Y1x6xvqcFXuyU3/Z7b16cNrbH4M6j6aXeirgW7AYjQSNfaZY2CCTTtnTNyxr3XbCirW0ffX7SClZ9gaKZQPG6/diEUINZNOixoqsIRKWCzUPG2KUzFj3b3BkhlsRYe7GRk8vaH39G74BjTh0GA8o1znYnqJYW+ZMRBJYp2V8zPvNZ7ba3cyiVdmUQoha59l/5D4iyV/IPfUUhM+BWLNeWR9cZ0a00bGaExAPxDt+Ou16866P3OjIT6zRkIOvpOcVyGGNn/kZWbQrr4ZopkJemhARmXjIDTbKuSmhg/fHcWODlNf9ccMXJqAXQuMGqFevX8oAGUPFn/G/bYgftogIARykTy9N+bOkOPTrOWwb6lljT3s+lCGoABibOwZ11hdUVOhEiqQ6DvB1drDYgA+hThI/yu6Bn+pyHkNu3kLFkeuYdDzURAj89cVIRbO8apXn8iCu5aVpIZKfVlUG9UTP7pM+AlnRpH5cqrvgK2nk1giOPf9yeYHefGDHtZwH7s//dq1e28lW95Os9RCVnkPOmhwmKPEQ0YNEU=
         * Expiration : 2020-05-06T07:38:36Z
         */

        public String AccessKeyId;
        public String AccessKeySecret;
        public String SecurityToken;
        public String Expiration;

        @Override
        public String toString() {
            return "DataBean{" +
                    ", AccessKeyId='" + AccessKeyId + '\'' +
                    ", AccessKeySecret='" + AccessKeySecret + '\'' +
                    ", SecurityToken='" + SecurityToken + '\'' +
                    ", Expiration='" + Expiration + '\'' +
                    '}';
        }
    }
}
