package roops.core.objects;

//@ model import org.jmlspecs.lang.*;


public class SinglyLinkedListContainsBug7 extends java.lang.Object {

  public /*@ nullable @*/ roops.core.objects.SinglyLinkedListNode roops_core_objects_SinglyLinkedListContainsBug7_header;
  /*@ invariant (\forall roops.core.objects.SinglyLinkedListNode n; \reach(this.roops_core_objects_SinglyLinkedListContainsBug7_header, roops.core.objects.SinglyLinkedListNode, roops_core_objects_SinglyLinkedListNode_next).has(((java.lang.Object)(n))); \reach(n.roops_core_objects_SinglyLinkedListNode_next, roops.core.objects.SinglyLinkedListNode, roops_core_objects_SinglyLinkedListNode_next).has(((java.lang.Object)(n)))  ==  false);
    @*/

  public SinglyLinkedListContainsBug7() {
    this.roops_core_objects_SinglyLinkedListContainsBug7_header = ((roops.core.objects.SinglyLinkedListNode)(null));
    {
    }
  }


  /*@ 
    @ requires true;
    @ ensures (\exists roops.core.objects.SinglyLinkedListNode n; \old(\reach(this.roops_core_objects_SinglyLinkedListContainsBug7_header, roops.core.objects.SinglyLinkedListNode, roops_core_objects_SinglyLinkedListNode_next)).has(((java.lang.Object)(n))); n.roops_core_objects_SinglyLinkedListNode_value  ==  valueParam) ==> (\result  ==  true);
    @ ensures (\result  ==  true) ==> (\exists roops.core.objects.SinglyLinkedListNode n; \old(\reach(this.roops_core_objects_SinglyLinkedListContainsBug7_header, roops.core.objects.SinglyLinkedListNode, roops_core_objects_SinglyLinkedListNode_next).has(((java.lang.Object)(n)))); n.roops_core_objects_SinglyLinkedListNode_value  ==  valueParam);
    @ ensures (\forall roops.core.objects.SinglyLinkedListNode n; \old(\reach(this.roops_core_objects_SinglyLinkedListContainsBug7_header, roops.core.objects.SinglyLinkedListNode, roops_core_objects_SinglyLinkedListNode_next).has(((java.lang.Object)(n)))); \old(n.roops_core_objects_SinglyLinkedListNode_value)  ==  n.roops_core_objects_SinglyLinkedListNode_value);
    @ signals (java.lang.RuntimeException e) false;
    @*/
  public boolean contains(/*@ nullable @*/ java.lang.Object valueParam) {
    java.lang.Object param_valueParam_0;

    param_valueParam_0 = valueParam;
    {
      boolean t_1;
      boolean t_2;
      boolean t_3;
      roops.core.objects.SinglyLinkedListNode var_1_current;
      boolean var_2_result;

      var_1_current = this.roops_core_objects_SinglyLinkedListContainsBug7_header;
      var_2_result = false;
      boolean var_3_ws_1;

      t_2 = var_2_result  ==  false;

      if (t_2) {
        {
          {
            t_3 = var_1_current  !=  null;
            if (t_3) {
              {
                t_1 = true;
              }
            } else {
              {
                t_1 = false;
              }
            }
          }
        }
      } else {
        {
          t_1 = false;
        }
      }
      var_3_ws_1 = t_1;

      /*@ decreasing \reach(var_1_current, roops.core.objects.SinglyLinkedListNode, roops_core_objects_SinglyLinkedListNode_next).int_size();
        @*/
      while (var_3_ws_1) {
        boolean t_6;
        boolean t_7;
        boolean t_8;
        boolean t_9;
        boolean t_10;
        boolean t_11;
        boolean t_12;
        boolean var_4_equalVal;

        t_7 = valueParam  !=  null;

        if (t_7) {
          {
            {
              t_8 = var_1_current.roops_core_objects_SinglyLinkedListNode_value  ==  null;
              if (t_8) {
                {
                  t_6 = true;
                }
              } else {
                {
                  t_6 = false;
                }
              }
            }
          }
        } else {
          {
            t_6 = false;
          }
        }

        if (t_6) {
          {
            {
              {
                {
                  {
                    var_4_equalVal = true;
                  }
                }
              }
            }
          }
        } else {
          {
            {
              {
                {
                  {
                    boolean t_5;

                    t_5 = valueParam  !=  null;
                    if (t_5) {
                      {
                        {
                          {
                            {
                              {
                                boolean t_4;

                                t_4 = valueParam  ==  var_1_current.roops_core_objects_SinglyLinkedListNode_value;
                                if (t_4) {
                                  {
                                    {
                                      {
                                        {
                                          {
                                            var_4_equalVal = true;
                                          }
                                        }
                                      }
                                    }
                                  }
                                } else {
                                  {
                                    {
                                      {
                                        {
                                          {
                                            var_4_equalVal = false;
                                          }
                                        }
                                      }
                                    }
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    } else {
                      {
                        {
                          {
                            {
                              {
                                var_4_equalVal = false;
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
        t_9 = var_4_equalVal  ==  true;

        if (t_9) {
          {
            {
              {
                {
                  {
                    var_2_result = true;
                  }
                }
              }
            }
          }
        }
        var_1_current = var_1_current.roops_core_objects_SinglyLinkedListNode_next;
        t_11 = var_2_result  ==  false;

        if (t_11) {
          {
            {
              t_12 = var_1_current  !=  null;
              if (t_12) {
                {
                  t_10 = true;
                }
              } else {
                {
                  t_10 = false;
                }
              }
            }
          }
        } else {
          {
            t_10 = false;
          }
        }
        var_3_ws_1 = t_10;
      }

      return var_2_result;
    }
  }

}
