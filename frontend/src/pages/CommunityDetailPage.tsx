import { useState, useEffect, useRef } from "react";
import { useParams, useNavigate } from "react-router-dom";
import MainLayout from "../components/layouts/MainLayout";
import { api } from "../utils/axios";
import { useAuthStore } from "../stores/authStore";

interface VoteDto {
  voteId: number;
  name: string;
  count: number;
  percentage: number;
}

interface CommentItem {
  commentId: number;
  parentId: number | null;
  content: string;
  authorNickname: string;
  likeCount: number;
  likedByMe: boolean;
  createdDate: string;
  replies: CommentItem[];
}

function formatRelativeTime(dateStr: string): string {
  const created = new Date(dateStr);
  const diffMs = Date.now() - created.getTime();
  const diffMin = Math.floor(diffMs / 1000 / 60);
  const diffHour = Math.floor(diffMin / 60);

  if (diffMin < 1) return "방금 전";
  if (diffMin < 60) return `${diffMin}분 전`;
  if (diffHour < 24) return `${diffHour}시간 전`;

  return created
    .toLocaleDateString("ko-KR", { year: "numeric", month: "2-digit", day: "2-digit" })
    .replace(/\. /g, "-")
    .replace(".", "");
}

interface ForumDetail {
  forumId: number;
  title: string;
  content: string;
  authorNickname: string;
  viewCount: number;
  likeCount: number;
  createdDate: string;
  lastModifiedDate: string;
  voteList: VoteDto[];
  myVoteId: number | null;
  totalVoteCount: number;
  voteDeadline: string | null;
  voteOpen: boolean;
}

export default function CommunityDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { isLoggedIn } = useAuthStore();

  const [forum, setForum] = useState<ForumDetail | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [voteLoading, setVoteLoading] = useState(false);

  const [comments, setComments] = useState<CommentItem[]>([]);
  const [commentText, setCommentText] = useState("");
  const [commentLoading, setCommentLoading] = useState(false);
  const textareaRef = useRef<HTMLTextAreaElement>(null);

  const [replyingTo, setReplyingTo] = useState<number | null>(null);
  const [replyText, setReplyText] = useState("");


  useEffect(() => {
    const fetchForum = async () => {
      try {
        const res = await api.get<{ success: boolean; data: ForumDetail }>(`/api/forum/${id}`, {
          _skipAuthRetry: true,
        } as any);
        setForum(res.data.data);
      } catch (err: any) {
        setError("게시글을 불러오는데 실패했습니다.");
      } finally {
        setLoading(false);
      }
    };

    fetchForum();
  }, [id]);

  useEffect(() => {
    const fetchComments = async () => {
      try {
        const res = await api.get<{ success: boolean; data: CommentItem[] }>(
          `/api/forum/${id}/comments`,
          { _skipAuthRetry: true } as any
        );
        setComments(res.data.data);
      } catch {
        // 댓글 로딩 실패는 조용히 처리
      }
    };
    if (id) fetchComments();
  }, [id]);

  const handleCommentSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!commentText.trim() || commentLoading) return;

    setCommentLoading(true);
    try {
      const res = await api.post<{ success: boolean; data: CommentItem }>(
        `/api/forum/${id}/comment`,
        { content: commentText.trim(), parentId: null }
      );
      setComments((prev) => [res.data.data, ...prev]);
      setCommentText("");
    } catch (err: any) {
      alert(err.response?.data?.error?.message || "댓글 등록에 실패했습니다.");
    } finally {
      setCommentLoading(false);
    }
  };

  const handleReplySubmit = async (parentId: number) => {
    if (!replyText.trim()) return;

    try {
      const res = await api.post<{ success: boolean; data: CommentItem }>(
        `/api/forum/${id}/comment`,
        { content: replyText.trim(), parentId }
      );
      const newReply = res.data.data;
      setComments((prev) =>
        prev.map((c) =>
          c.commentId === parentId
            ? { ...c, replies: [...(c.replies ?? []), newReply] }
            : c
        )
      );
      setReplyText("");
      setReplyingTo(null);
    } catch (err: any) {
      alert(err.response?.data?.error?.message || "답글 등록에 실패했습니다.");
    }
  };

  const handleCommentLike = async (commentId: number, parentId: number | null) => {
    if (!isLoggedIn) {
      if (confirm("로그인이 필요합니다. 로그인 페이지로 이동하시겠습니까?")) {
        navigate("/login");
      }
      return;
    }
    try {
      const res = await api.post<{ success: boolean; data: CommentItem }>(
        `/api/comment/${commentId}/like`
      );
      const updated = res.data.data;
      setComments((prev) =>
        prev.map((c) => {
          if (parentId === null && c.commentId === commentId) return { ...c, ...updated };
          if (parentId !== null && c.commentId === parentId) {
            return {
              ...c,
              replies: (c.replies ?? []).map((r) =>
                r.commentId === commentId ? { ...r, ...updated } : r
              ),
            };
          }
          return c;
        })
      );
    } catch {
      // 좋아요 실패 무시
    }
  };

  const handleVote = async (voteId: number) => {
    if (!isLoggedIn) {
      if (confirm("로그인이 필요합니다. 로그인 페이지로 이동하시겠습니까?")) {
        navigate("/login");
      }
      return;
    }

    if (forum?.voteOpen === false) return; // 투표 마감
    if (forum?.myVoteId === voteId) return; // 같은 항목 재클릭
    if (voteLoading) return;

    setVoteLoading(true);
    try {
      const res = await api.post<{ success: boolean; data: ForumDetail }>(`/api/forum/${id}/vote`, {
        voteId,
      });
      setForum(res.data.data);
    } catch (err: any) {
      const message =
        err.response?.data?.error?.message || err.message || "투표에 실패했습니다.";
      alert(message);
    } finally {
      setVoteLoading(false);
    }
  };

  if (loading) {
    return (
      <MainLayout>
        <div className="text-center py-20 text-gray-500">로딩 중...</div>
      </MainLayout>
    );
  }

  if (error || !forum) {
    return (
      <MainLayout>
        <div className="text-center py-20">
          <p className="text-red-500 mb-4">
            {error ?? "게시글을 찾을 수 없습니다."}
          </p>
          <button
            onClick={() => navigate("/community")}
            className="text-blue-600 hover:underline"
          >
            목록으로 돌아가기
          </button>
        </div>
      </MainLayout>
    );
  }

  const formattedDate = new Date(forum.createdDate).toLocaleDateString(
    "ko-KR",
    { year: "numeric", month: "2-digit", day: "2-digit" }
  );

  const hasVoted = forum.myVoteId !== null;
  const showResults = hasVoted || !isLoggedIn;
  const voteOpen = forum.voteOpen !== false;

  const formattedDeadline = forum.voteDeadline
    ? new Date(forum.voteDeadline).toLocaleString("ko-KR", {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
        hour: "2-digit",
        minute: "2-digit",
      })
    : null;

  return (
    <MainLayout>
      <div className="max-w-3xl mx-auto space-y-6">
        <div className="space-y-6">
          <div className="bg-white p-8 rounded-2xl shadow">
            <h2 className="text-2xl font-bold mb-4">{forum.title}</h2>
            <div className="text-sm text-gray-500 mb-6 border-b pb-4">
              작성자: {forum.authorNickname} · {formattedDate} · 조회{" "}
              {forum.viewCount.toLocaleString()}
            </div>

            <p className="text-gray-700 leading-relaxed mb-8 whitespace-pre-wrap">
              {forum.content}
            </p>

            {/* 투표 섹션 */}
            {forum.voteList.length > 0 && (
              <div className="bg-gray-50 p-6 rounded-xl border border-gray-200">
                <div className="flex items-center justify-between mb-4">
                  <div className="flex items-center gap-2">
                    <h3 className="font-bold">📊 투표</h3>
                    {!voteOpen && (
                      <span className="text-xs text-red-500 font-semibold">마감됨</span>
                    )}
                  </div>
                  <div className="flex items-center gap-3">
                    {formattedDeadline && (
                      <span className="text-xs text-gray-400">
                        마감: {formattedDeadline}
                      </span>
                    )}
                    <span className="text-sm text-gray-500">
                      총 {forum.totalVoteCount.toLocaleString()}명 참여
                    </span>
                  </div>
                </div>

                <div className="space-y-3">
                  {forum.voteList.map((vote) => {
                    const isMyVote = forum.myVoteId === vote.voteId;
                    const canClick = isLoggedIn && voteOpen && !isMyVote;

                    // 투표 전 + 로그인 상태: 선택 가능한 버튼
                    if (!showResults) {
                      return (
                        <button
                          key={vote.voteId}
                          onClick={() => handleVote(vote.voteId)}
                          disabled={voteLoading || !voteOpen}
                          className="w-full p-4 rounded-xl border-2 border-blue-200 bg-white
                                     hover:bg-blue-50 hover:border-blue-400 transition-all text-left
                                     disabled:opacity-50"
                        >
                          <span className="font-bold">{vote.name}</span>
                        </button>
                      );
                    }

                    // 투표 후 / 비로그인: 결과 표시 (로그인+미마감 시 클릭으로 변경 가능)
                    return (
                      <div
                        key={vote.voteId}
                        onClick={() => canClick && handleVote(vote.voteId)}
                        className={`relative overflow-hidden rounded-xl border-2 p-4 transition-all ${
                          isMyVote
                            ? "border-blue-600 bg-blue-50"
                            : "border-gray-200 bg-white"
                        } ${canClick ? "cursor-pointer hover:border-blue-400" : ""}`}
                      >
                        {/* 퍼센트 바 */}
                        <div
                          className={`absolute inset-y-0 left-0 transition-all duration-500 ${
                            isMyVote ? "bg-blue-200/50" : "bg-gray-200/50"
                          }`}
                          style={{ width: `${vote.percentage}%` }}
                        />

                        {/* 내용 */}
                        <div className="relative flex items-center justify-between">
                          <div className="flex items-center gap-2">
                            {isMyVote && (
                              <span className="text-blue-600 text-sm">✓</span>
                            )}
                            <span
                              className={`font-bold ${
                                isMyVote ? "text-blue-700" : "text-gray-800"
                              }`}
                            >
                              {vote.name}
                            </span>
                          </div>
                          <div className="flex items-center gap-3">
                            <span className="text-sm text-gray-500">
                              {vote.count.toLocaleString()}표
                            </span>
                            <span
                              className={`font-bold text-sm ${
                                isMyVote ? "text-blue-600" : "text-gray-700"
                              }`}
                            >
                              {vote.percentage}%
                            </span>
                          </div>
                        </div>
                      </div>
                    );
                  })}
                </div>

                {!voteOpen && (
                  <p className="text-xs text-red-400 mt-3 text-center">
                    투표가 마감되었습니다
                  </p>
                )}
                {hasVoted && voteOpen && (
                  <p className="text-xs text-gray-400 mt-3 text-center">
                    다른 항목을 클릭하면 투표를 변경할 수 있습니다
                  </p>
                )}
                {!isLoggedIn && voteOpen && (
                  <p className="text-xs text-gray-400 mt-3 text-center">
                    로그인 후 투표에 참여할 수 있습니다
                  </p>
                )}
              </div>
            )}
          </div>

          {/* 실시간 토론 (댓글) */}
          <div className="bg-white p-8 rounded-2xl shadow">
            <h3 className="text-lg font-bold mb-6 flex items-center gap-2">
              💬 실시간 토론
              <span className="text-sm font-normal text-gray-400">
                {comments.length}개
              </span>
            </h3>

            {/* 댓글 입력 */}
            {isLoggedIn ? (
              <form onSubmit={handleCommentSubmit} className="mb-6">
                <textarea
                  ref={textareaRef}
                  value={commentText}
                  onChange={(e) => setCommentText(e.target.value)}
                  placeholder="토론에 참여해보세요"
                  rows={3}
                  maxLength={500}
                  disabled={commentLoading}
                  className="w-full border border-gray-200 rounded-xl p-3 text-sm resize-none focus:outline-none focus:ring-2 focus:ring-blue-300 disabled:opacity-50"
                />
                <div className="flex justify-between items-center mt-2">
                  <span className="text-xs text-gray-400">
                    {commentText.length}/500
                  </span>
                  <button
                    type="submit"
                    disabled={!commentText.trim() || commentLoading}
                    className="bg-blue-600 text-white text-sm px-5 py-2 rounded-lg hover:bg-blue-700 transition disabled:opacity-40"
                  >
                    {commentLoading ? "등록 중..." : "등록"}
                  </button>
                </div>
              </form>
            ) : (
              <div
                onClick={() => navigate("/login")}
                className="mb-6 border border-dashed border-gray-200 rounded-xl p-4 text-center text-sm text-gray-400 cursor-pointer hover:bg-gray-50 transition"
              >
                로그인 후 토론에 참여할 수 있습니다
              </div>
            )}

            {/* 댓글 목록 */}
            <div className="space-y-5">
              {comments.length === 0 ? (
                <p className="text-center text-gray-400 text-sm py-8">
                  아직 토론이 없습니다. 첫 번째 의견을 남겨보세요!
                </p>
              ) : (
                comments.map((comment) => (
                  <div key={comment.commentId}>
                    {/* 댓글 */}
                    <div className="flex gap-3">
                      <div className="flex-shrink-0 w-8 h-8 rounded-full bg-blue-100 flex items-center justify-center text-xs font-bold text-blue-600">
                        {comment.authorNickname.charAt(0)}
                      </div>
                      <div className="flex-1">
                        <div className="flex items-center gap-2 mb-1">
                          <span className="text-sm font-semibold">{comment.authorNickname}</span>
                          {comment.authorNickname === forum.authorNickname && (
                            <span className="text-xs bg-blue-100 text-blue-600 font-semibold px-1.5 py-0.5 rounded">작성자</span>
                          )}
                          <span className="text-xs text-gray-400">{formatRelativeTime(comment.createdDate)}</span>
                        </div>
                        <p className="text-sm text-gray-700 leading-relaxed whitespace-pre-wrap mb-1">
                          {comment.content}
                        </p>
                        {isLoggedIn && (
                          <button
                            onClick={() =>
                              setReplyingTo(replyingTo === comment.commentId ? null : comment.commentId)
                            }
                            className="text-xs text-gray-400 hover:text-blue-500 transition"
                          >
                            {replyingTo === comment.commentId ? "취소" : "답글"}
                          </button>
                        )}
                      </div>
                      <button
                        onClick={() => handleCommentLike(comment.commentId, null)}
                        className={`flex-shrink-0 flex flex-col items-center gap-0.5 px-2 py-1 rounded-lg text-xs transition ${
                          comment.likedByMe ? "text-red-500 bg-red-50" : "text-gray-400 hover:text-red-400 hover:bg-red-50"
                        }`}
                      >
                        <span className="text-base leading-none">{comment.likedByMe ? "❤️" : "🤍"}</span>
                        <span className="font-medium">{comment.likeCount}</span>
                      </button>
                    </div>

                    {/* 답글 입력 */}
                    {replyingTo === comment.commentId && (
                      <div className="ml-11 mt-2 flex gap-2">
                        <textarea
                          value={replyText}
                          onChange={(e) => setReplyText(e.target.value)}
                          placeholder={`${comment.authorNickname}님에게 답글`}
                          rows={2}
                          maxLength={500}
                          className="flex-1 border border-gray-200 rounded-xl p-2 text-sm resize-none focus:outline-none focus:ring-2 focus:ring-blue-300"
                        />
                        <button
                          onClick={() => handleReplySubmit(comment.commentId)}
                          disabled={!replyText.trim()}
                          className="self-end bg-blue-600 text-white text-xs px-4 py-2 rounded-lg hover:bg-blue-700 transition disabled:opacity-40"
                        >
                          등록
                        </button>
                      </div>
                    )}

                    {/* 대댓글 목록 */}
                    {comment.replies?.length > 0 && (
                      <div className="ml-11 mt-3 space-y-3 border-l-2 border-gray-100 pl-4">
                        {comment.replies.map((reply) => (
                          <div key={reply.commentId} className="flex gap-3">
                            <div className="flex-shrink-0 w-7 h-7 rounded-full bg-gray-100 flex items-center justify-center text-xs font-bold text-gray-500">
                              {reply.authorNickname.charAt(0)}
                            </div>
                            <div className="flex-1">
                              <div className="flex items-center gap-2 mb-1">
                                <span className="text-sm font-semibold">{reply.authorNickname}</span>
                                {reply.authorNickname === forum.authorNickname && (
                                  <span className="text-xs bg-blue-100 text-blue-600 font-semibold px-1.5 py-0.5 rounded">작성자</span>
                                )}
                                <span className="text-xs text-gray-400">{formatRelativeTime(reply.createdDate)}</span>
                              </div>
                              <p className="text-sm text-gray-700 leading-relaxed whitespace-pre-wrap">
                                {reply.content}
                              </p>
                            </div>
                            <button
                              onClick={() => handleCommentLike(reply.commentId, comment.commentId)}
                              className={`flex-shrink-0 flex flex-col items-center gap-0.5 px-2 py-1 rounded-lg text-xs transition ${
                                reply.likedByMe ? "text-red-500 bg-red-50" : "text-gray-400 hover:text-red-400 hover:bg-red-50"
                              }`}
                            >
                              <span className="text-base leading-none">{reply.likedByMe ? "❤️" : "🤍"}</span>
                              <span className="font-medium">{reply.likeCount}</span>
                            </button>
                          </div>
                        ))}
                      </div>
                    )}
                  </div>
                ))
              )}
            </div>
          </div>
        </div>
      </div>
    </MainLayout>
  );
}
